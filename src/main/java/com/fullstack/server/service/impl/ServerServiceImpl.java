package com.fullstack.server.service.impl;

import com.fullstack.server.enumeration.Status;
import com.fullstack.server.model.Server;
import com.fullstack.server.repo.ServerRepo;
import com.fullstack.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;

import static java.lang.Boolean.*;
import static org.springframework.data.domain.PageRequest.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {

    private  final ServerRepo serverRepo;

    @Override
    public Server create(Server server) {
        log.info("Saving new server : {}", server.getName());
        server.setImageUrl(setServerImageUrl(server.getStatus() != null ? server.getStatus() : Status.SERVER_DOWN));
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server Ip : {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(1000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        server.setImageUrl(setServerImageUrl(server.getStatus()));
        serverRepo.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepo.findAll(of(0,limit)).toList();
    }

    @Override
    public Optional<Server> get(Long id) {
        log.info("Fetching server by Id : {}", id);
        return serverRepo.findById(id);
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server : {}", server.getName());
        server.setImageUrl(setServerImageUrl(server.getStatus() != null ? server.getStatus() : Status.SERVER_DOWN));
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by Id : {}", id);
        serverRepo.deleteById(id);
        return TRUE;
    }

    private String setServerImageUrl(Status status) {
        String imageName = status.name().equals(Status.SERVER_UP.name()) ? "server1.png": "server2.png";
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/"+ imageName).toUriString();
    }
}
